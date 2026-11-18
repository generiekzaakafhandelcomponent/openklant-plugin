import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FunctionConfigurationComponent, FunctionConfigurationData, PluginTranslatePipeModule} from '@valtimo/plugin';
import {BehaviorSubject, combineLatest, Observable, Subscription, take} from 'rxjs';
import {AsyncPipe, NgIf} from '@angular/common';
import {FormModule, FormOutput, InputModule, ParagraphModule} from '@valtimo/components';
import {GetOrCreateDigitaalAdresConfig} from '../../models/get-or-create-digitaal-adres-config';

@Component({
    selector: 'openklant-get-or-create-digitaal-adres',
    standalone: true,
    imports: [AsyncPipe, FormModule, InputModule, NgIf, ParagraphModule, PluginTranslatePipeModule],
    templateUrl: './get-or-create-digitaal-adres.component.html'
})
export class GetOrCreateDigitaalAdresComponent implements FunctionConfigurationComponent, OnInit, OnDestroy {
    @Input() save$: Observable<void>;
    @Input() disabled$: Observable<boolean>;
    @Input() pluginId: string;
    @Input() prefillConfiguration$?: Observable<GetOrCreateDigitaalAdresConfig>;
    @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
    @Output() configuration: EventEmitter<FunctionConfigurationData> = new EventEmitter<FunctionConfigurationData>();

    private saveSubscription!: Subscription;

    private readonly config$ = new BehaviorSubject<GetOrCreateDigitaalAdresConfig | null>(null);
    private readonly valid$ = new BehaviorSubject<boolean>(false);

    ngOnInit(): void {
        this.openSaveSubscription();
    }

    ngOnDestroy(): void {
        this.saveSubscription?.unsubscribe();
    }

    formValueChange(formOutput: FormOutput): void {
        const config = formOutput as GetOrCreateDigitaalAdresConfig;
        this.config$.next(config);
        this.handleValid(config);
    }

    private handleValid(formOutput: GetOrCreateDigitaalAdresConfig): void {
        const valid =
            !!formOutput.resultPvName &&
            (!!formOutput.verstrektDoorPartij || !!formOutput.verstrektDoorBetrokkene) &&
            !!formOutput.adres &&
            !!formOutput.soortDigitaalAdres;

        this.valid$.next(valid);
        this.valid.emit(valid);
    }

    private openSaveSubscription(): void {
        this.saveSubscription = this.save$?.subscribe(() => {
            combineLatest([this.config$, this.valid$])
                .pipe(take(1))
                .subscribe(([config, valid]) => {
                    if (valid && config) {
                        this.configuration.emit(config);
                    }
                });
        });
    }
}

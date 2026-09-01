import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FunctionConfigurationComponent, FunctionConfigurationData, PluginTranslatePipeModule} from '@valtimo/plugin';
import {BehaviorSubject, combineLatest, Observable, Subscription, take} from 'rxjs';
import {DigitaalAdresConfig} from '../../models/digitaal-adres-config';
import {AsyncPipe, NgIf} from '@angular/common';
import {FormModule, FormOutput, InputModule} from '@valtimo/components';

@Component({
    selector: 'create-digitaal-adres',
    imports: [AsyncPipe, FormModule, InputModule, NgIf, PluginTranslatePipeModule],
    standalone: true,
    templateUrl: './create-digitaal-adres.component.html'
})
export class CreateDigitaalAdresComponent implements FunctionConfigurationComponent, OnInit, OnDestroy {
    @Input() save$: Observable<void>;
    @Input() disabled$: Observable<boolean>;
    @Input() pluginId: string;
    @Input() prefillConfiguration$?: Observable<DigitaalAdresConfig>;
    @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
    @Output() configuration: EventEmitter<FunctionConfigurationData> = new EventEmitter<FunctionConfigurationData>();

    private saveSubscription!: Subscription;

    private readonly config$ = new BehaviorSubject<DigitaalAdresConfig | null>(null);
    private readonly valid$ = new BehaviorSubject<boolean>(false);

    ngOnInit(): void {
        this.openSaveSubscription();
    }

    ngOnDestroy(): void {
        this.saveSubscription?.unsubscribe();
    }

    formValueChange(formOutput: FormOutput): void {
        this.config$.next(formOutput as DigitaalAdresConfig);
        this.handleValid(formOutput as DigitaalAdresConfig);
    }

    private handleValid(formOutput: DigitaalAdresConfig): void {
        const valid =
            !!formOutput.resultPvName &&
            !!formOutput.verstrektDoorBetrokkene &&
            !!formOutput.verstrektDoorPartij &&
            !!formOutput.adres &&
            !!formOutput.soortDigitaalAdres &&
            !!formOutput.isStandaardAdres &&
            !!formOutput.omschrijving &&
            !!formOutput.referentie &&
            !!formOutput.verificatieDatum;

        this.valid$.next(valid);
        this.valid.emit(valid);
    }

    private openSaveSubscription(): void {
        this.saveSubscription = this.save$?.subscribe(save => {
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

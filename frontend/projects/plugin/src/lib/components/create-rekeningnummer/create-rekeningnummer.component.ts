import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FunctionConfigurationComponent, FunctionConfigurationData, PluginTranslatePipeModule} from '@valtimo/plugin';
import {BehaviorSubject, combineLatest, Observable, Subscription, take} from 'rxjs';
import {AsyncPipe, NgIf} from '@angular/common';
import {FormModule, FormOutput, InputModule, ParagraphModule, TooltipModule} from '@valtimo/components';
import {CreateRekeningnummerConfig} from '../../models/create-rekeningnummer-config';

@Component({
    selector: 'openklant-create-rekeningnummer',
    standalone: true,
    imports: [
        AsyncPipe,
        FormModule,
        InputModule,
        NgIf,
        ParagraphModule,
        PluginTranslatePipeModule,
        TooltipModule
    ],
    templateUrl: './create-rekeningnummer.component.html'
})
export class CreateRekeningnummerComponent implements FunctionConfigurationComponent, OnInit, OnDestroy {
    @Input() save$: Observable<void>;
    @Input() disabled$: Observable<boolean>;
    @Input() pluginId: string;
    @Input() prefillConfiguration$?: Observable<CreateRekeningnummerConfig>;
    @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
    @Output() configuration: EventEmitter<FunctionConfigurationData> = new EventEmitter<FunctionConfigurationData>();

    private saveSubscription!: Subscription;

    private readonly config$ = new BehaviorSubject<CreateRekeningnummerConfig | null>(null);
    private readonly valid$ = new BehaviorSubject<boolean>(false);

    ngOnInit(): void {
        this.openSaveSubscription();
    }

    ngOnDestroy(): void {
        this.saveSubscription?.unsubscribe();
    }

    formValueChange(formOutput: FormOutput): void {
        const config = formOutput as CreateRekeningnummerConfig;
        this.config$.next(config);
        this.handleValid(config);
    }

    private handleValid(formOutput: CreateRekeningnummerConfig): void {
        const valid = 
            !!formOutput.iban &&
            !!formOutput.resultPvName;

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

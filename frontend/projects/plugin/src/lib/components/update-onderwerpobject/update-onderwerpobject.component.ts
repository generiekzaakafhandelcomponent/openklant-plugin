import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FunctionConfigurationComponent, FunctionConfigurationData, PluginTranslatePipeModule} from '@valtimo/plugin';
import {BehaviorSubject, combineLatest, Observable, Subscription, take} from 'rxjs';
import {AsyncPipe, NgIf} from '@angular/common';
import {FormModule, FormOutput, InputModule, ParagraphModule, TooltipModule} from '@valtimo/components';
import {UpdateOnderwerpobjectConfig} from '../../models/update-onderwerpobject-config';

@Component({
    selector: 'openklant-update-onderwerpobject',
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
    templateUrl: './update-onderwerpobject.component.html'
})
export class UpdateOnderwerpobjectComponent implements FunctionConfigurationComponent, OnInit, OnDestroy {
    @Input() save$: Observable<void>;
    @Input() disabled$: Observable<boolean>;
    @Input() pluginId: string;
    @Input() prefillConfiguration$?: Observable<UpdateOnderwerpobjectConfig>;
    @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
    @Output() configuration: EventEmitter<FunctionConfigurationData> = new EventEmitter<FunctionConfigurationData>();

    private saveSubscription!: Subscription;

    private readonly config$ = new BehaviorSubject<UpdateOnderwerpobjectConfig | null>(null);
    private readonly valid$ = new BehaviorSubject<boolean>(false);

    ngOnInit(): void {
        this.openSaveSubscription();
    }

    ngOnDestroy(): void {
        this.saveSubscription?.unsubscribe();
    }

    formValueChange(formOutput: FormOutput): void {
        const config = formOutput as UpdateOnderwerpobjectConfig;
        this.config$.next(config);
        this.handleValid(config);
    }

    private handleValid(formOutput: UpdateOnderwerpobjectConfig): void {
        const valid = 
            !!formOutput.uuid &&
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
